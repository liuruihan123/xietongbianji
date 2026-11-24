import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TuneInfoVO, TuneInfoForm, TuneInfoQuery } from '@/api/system/tuneInfo/types';
import { TuneAttachVO } from '@/api/system/tuneAttach/types';

/**
 * 查询精调管理列表
 * @param query
 * @returns {*}
 */

export const listTuneInfo = (query?: TuneInfoQuery): AxiosPromise<TuneInfoVO[]> => {
  return request({
    url: '/system/tuneInfo/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询精调管理详细
 * @param id
 */
export const getTuneInfo = (id: string | number): AxiosPromise<TuneInfoVO> => {
  return request({
    url: '/system/tuneInfo/' + id,
    method: 'get'
  });
};

/**
 * 新增精调管理
 * @param data
 */
export const addTuneInfo = (data: TuneInfoForm) => {
  return request({
    url: '/system/tuneInfo',
    method: 'post',
    data: data
  });
};

/**
 * 修改精调管理
 * @param data
 */
export const updateTuneInfo = (data: TuneInfoForm) => {
  return request({
    url: '/system/tuneInfo',
    method: 'put',
    data: data
  });
};

/**
 * 删除精调管理
 * @param ftid
 */
// export const delTuneInfo = (ftid: string | number | Array<string | number>) => {
//   return request({
//     url: '/system/tuneInfo/' + ftid,
//     method: 'delete'
//   });
// };
export const delTuneInfo = (ftid: string | number | Array<string | number>) => {
  let idString: string;
  if (typeof ftid === 'string' || typeof ftid === 'number') {
    idString = String(ftid);
  } else if (Array.isArray(ftid)) {
    idString = ftid.map((id) => String(id)).join(',');
  } else {
    throw new Error('Invalid ftid type');
  }

  return request({
    url: '/system/tuneInfo/' + idString,
    method: 'delete'
  });
};

/**
 * 删除微调附件
 * @param id
 */
export const delTuneAttach = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/tuneAttach/' + id,
    method: 'delete'
  });
};

/**
 * 使用状态修改
 */
export const changeUseStatus = (id: string | number, useStatus: string) => {
  const data = {
    id,
    useStatus
  };
  return request({
    url: '/system/tuneInfo/changeUseStatus',
    method: 'put',
    data: data
  });
};
/**
 * 内容转化
 */
export const fileContentToFtContent = (id: string | number, promptsStoreId: string | number) => {
  const data = {
    id,
    promptsStoreId
  };
  return request({
    url: '/system/tuneInfo/fileContentToFtContent',
    method: 'put',
    data: data
  });
};

/**
 * 内容转化
 */
export const updateFtContent = (id: string | number, ftContent: string) => {
  const data = {
    id,
    ftContent
  };
  return request({
    url: '/system/tuneInfo/updateFtContent',
    method: 'put',
    data: data
  });
};
/**
 * 上传微调数据
 */
export const uploadFinetuneData = (id: string | number, ftId: string) => {
  const data = {
    id,
    ftId
  };
  console.log(data);
  return request({
    url: '/system/tuneInfo/uploadFinetuneData',
    method: 'put',
    data: data
  });
};

// getAttachStatus
/**
 * 获取微调附件状态
 */
export const getAttachStatus = (id: string | number): AxiosPromise<TuneAttachVO> => {
  const data = {
    id
  };
  return request({
    url: '/system/tuneInfo/getAttachStatus',
    method: 'put',
    data: data
  });
};

/**
 * 获取附件微调状态中
 */
export const getAttachFtStatus = (ftid: string | number): AxiosPromise<TuneAttachVO> => {
  const data = {
    ftid
  };
  return request({
    url: '/system/tuneInfo/getAttachFtStatus',
    method: 'put',
    data: data
  });
};

/**
 * 检查微调内容
 */
export const checkFtContent = (id: string | number, ftContent: string): AxiosPromise<TuneAttachVO> => {
  const data = {
    id,
    ftContent
  };
  return request({
    url: '/system/tuneInfo/checkFtContent',
    method: 'put',
    data: data
  });
};
