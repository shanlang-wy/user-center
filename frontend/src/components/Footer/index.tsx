import {DefaultFooter} from '@ant-design/pro-layout';

const Footer: React.FC = () => {
  const defaultMessage = '用户中心';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[]}
    />
  );
};

export default Footer;
